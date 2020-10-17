import { User } from '@/lib/user/User'
import database from '@/loaders/postgres'
import { Model } from '@/Model/Model'

export class RoleModel extends Model {
    constructor() {
        super('roles')
    }

    async getAllRoles() {
        return await database.query(`SELECT * FROM ${this.table}`)
    }

    async getRole(name: string) {
        return await database.query(`SELECT * FROM ${this.table} WHERE role_name = $1`, [name])
    }

    async addNewRole(name: string) {
        return await database.query(
            `INSERT INTO ${this.table} (role_name) VALUES ($1) RETURNING *`,
            [name]
        )
    }
}
