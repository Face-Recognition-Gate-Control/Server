import { Station } from '@/lib/station/Station'
import { Model } from '@/Model/Model'
import { Client } from 'pg'

export class StationModel extends Model {
    constructor(database: Client) {
        super('stations', database)
    }

    async getAllStations() {
        return await this.database.query(`SELECT * FROM ${this.table}`)
    }

    async getStationById(id: string) {
        return await this.database.query(`SELECT * FROM ${this.table} WHERE id = $1`, [id])
    }

    async createStation(station: Station) {
        // TODO: FIX TYPO IN CHEKIN DATABASE!!
        return await this.database.query(
            `INSERT INTO ${this.table} (id, login_key, station_name, last_chekin) VALUES ($1, $2, $3, $4) RETURNING *`,
            [station.id, station.login_key, station.station_name, station.last_checkin]
        )
    }
}
