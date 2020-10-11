import database from '@/loaders/postgres'
import { Model } from '@/Model/Model'

export class UserModel extends Model {
    constructor() {
        super('users')
    }

    async getAllUsers() {
        return await database.query(`SELECT * FROM ${this.table}`)
    }

    async getUserById(id: number) {
        return await database.query(`SELECT * FROM ${this.table} WHERE id = $1`, [id])
    }
}
