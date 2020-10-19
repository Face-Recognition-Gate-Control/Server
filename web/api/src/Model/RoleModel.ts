import { User } from '@/lib/user/User'
import { Model } from '@/Model/Model'
import { Client } from 'pg'

export class RoleModel extends Model {
    constructor(database: Client) {
        super('roles', database)
    }

    async getAllRoles() {
        return await this.database.query(`SELECT * FROM ${this.table}`)
    }

    async getRole(name: string) {
        return await this.database.query(`SELECT * FROM ${this.table} WHERE role_name = $1`, [name])
    }

    async addNewRole(name: string) {
        return await this.database.query(
            `INSERT INTO ${this.table} (role_name) VALUES ($1) RETURNING *`,
            [name]
        )
    }
}
