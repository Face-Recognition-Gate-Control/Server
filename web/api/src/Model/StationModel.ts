import { User } from '@/lib/user/User'
import database from '@/loaders/postgres'
import { Model } from '@/Model/Model'

export class StationModel extends Model {
    constructor() {
        super('stations')
    }

    async getAllStations() {
        return await database.query(`SELECT QW * FROM ${this.table}`)
    }

    async getStationById(id: string) {
        return await database.query(`SELECT * FROM ${this.table} WHERE id = $1`, [id])
    }

    // async createStation(station: Station) {
    //     return await database.query(
    //         `INSERT INTO ${this.table} (firstname, lastname, email, telephone, password) VALUES ($1, $2,$3,$4,$5) RETURNING *`,
    //         [user.firstname, user.lastname, user.email, user.telephone, user.password]
    //     )
    // }
}
