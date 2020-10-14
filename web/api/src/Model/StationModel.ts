import { Station } from '@/lib/station/Station'
import { User } from '@/lib/user/User'
import database from '@/loaders/postgres'
import { Model } from '@/Model/Model'

export class StationModel extends Model {
    constructor() {
        super('stations')
    }

    async getAllStations() {
        return await database.query(`SELECT * FROM ${this.table}`)
    }

    async getStationById(id: string) {
        return await database.query(`SELECT * FROM ${this.table} WHERE id = $1`, [id])
    }

    async createStation(station: Station) {
        // TODO: FIX TYPO IN CHEKIN DATABASE!!
        return await database.query(
            `INSERT INTO ${this.table} (id, login_key, station_name, last_chekin) VALUES ($1, $2, $3, $4) RETURNING *`,
            [station.id, station.login_key, station.station_name, station.last_checkin]
        )
    }
}
