import { NewUser } from '@/lib/user/User'
import database from '@/loaders/postgres'
import { Model } from '@/Model/Model'
import { Client } from 'pg'

export class UserModel extends Model {
    private _rolesTable = 'user_roles'
    private _enteredEventsTable = 'user_enter_events'
    private _new_user_table = 'new_user_queue'

    constructor(database: Client) {
        super('users', database)
    }

    async getUserRegistrationData(token: string) {
        return await this.database.query(
            `SELECT * FROM ${this._new_user_table} WHERE tmp_id = $1`,
            [token]
        )
    }

    async getAllUsers() {
        return await this.database.query(`SELECT * FROM ${this.table}`)
    }

    async getRolesForUser(id: string) {
        return await this.database.query(`SELECT * FROM ${this._rolesTable} WHERE user_id = $1`, [
            id,
        ])
    }

    async getUserWithRoles(id: string) {
        return await this.database.query(
            `SELECT *, (SELECT array_agg(role_name)
                 FROM ${this._rolesTable} WHERE user_id = $1) AS roles FROM ${this.table} WHERE id = $1`,
            [id]
        )
    }

    async getUserEnterEvents(id: string) {
        return await this.database.query(
            `SELECT * FROM ${this._enteredEventsTable} WHERE user_id = $1`,
            [id]
        )
    }

    async getUserById(id: string) {
        return await this.database.query(`SELECT * FROM ${this.table} WHERE id = $1`, [id])
    }

    async getUserByMail(email: string) {
        return await this.database.query(`SELECT * FROM ${this.table} WHERE email = $1 LIMIT 1`, [
            email,
        ])
    }

    async createUser(user: NewUser) {
        try {
            await this.database.query('BEGIN')

            const registrationData = await this.getUserRegistrationData(user.registration_token)
            if (registrationData.rowCount != 1) throw new Error('Registration token does not exist')
            const tokenData = registrationData.rows[0]
            this.moveFromRegistrationUserQueue(tokenData)

            const returnedUser = await this.database.query(
                `INSERT INTO ${this.table} (id, firstname, lastname, email, telephone, password) VALUES ($1, $2,$3,$4,$5, $6) RETURNING *;`,
                [
                    tokenData.tmp_id,
                    user.firstname,
                    user.lastname,
                    user.email,
                    user.telephone,
                    user.password,
                ]
            )

            await this.database.query('COMMIT')
            console.log('COMPLETE')

            return returnedUser
        } catch (error) {
            await this.database.query('ROLLBACK')
        }
    }

    private async moveFromRegistrationUserQueue(registrationQueue: any) {
        await this.database.query(`DELETE FROM new_user_queue WHERE tmp_id = $1`, [
            registrationQueue.tmp_id,
        ])
        await this.database.query(
            `INSERT INTO login_referance (user_id, face_vec, file_name) VALUES
            ($1,$2,$3)`,
            [registrationQueue.tmp_id, registrationQueue.face_vec, registrationQueue.file_name]
        )
    }
}
