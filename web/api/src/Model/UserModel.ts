import { User } from '@/lib/user/User'
import database from '@/loaders/postgres'
import { Model } from '@/Model/Model'

export class UserModel extends Model {
    constructor() {
        super('users')
    }

    async getAllUsers() {
        return await database.query(`SELECT QW * FROM ${this.table}`)
    }

    async getUserById(id: number) {
        return await database.query(`SELECT * FROM ${this.table} WHERE id = $1`, [id])
    }

    async createUser(user: User) {
        return await database.query(
            `INSERT INTO ${this.table} (firstname, lastname, email, telephone, password) VALUES ($1, $2,$3,$4,$5) RETURNING *`,
            [user.firstname, user.lastname, user.email, user.telephone, user.password]
        )
    }
}
