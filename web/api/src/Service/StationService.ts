import { StationModel } from '@/Model/StationModel'
import logger from '@/loaders/logger'
import { Station, StationType } from '@/lib/station/Station'
import { v4 as UUID } from 'uuid'

/**
 * Station serivce performes the business logic between Grapgql/API and Database.
 */
export class StationService {
    private _model: StationModel

    constructor(model: StationModel) {
        this._model = model
    }

    /**
     * Returns a station, or null if a station is not found
     * @param id id of the station to get
     */
    async getStationById(id: string) {
        try {
            let q = (await this._model.getStationById(id)).rows[0]
        } catch (error) {
            logger.error(error)
        }
    }

    /**
     * Returns all stations or null of no stations are found
     */
    async getAllStations() {
        try {
            let q = (await this._model.getAllStations()).rows
            console.log(q)
            return q
        } catch (error) {
            logger.error(error)
        }
    }

    /**
     * Creates a new station and returns the newly created instance
     * @param station station fields
     */
    async createStation(station: StationType) {
        // TODO : VALIDATION, HASH PASSWORD
        try {
            station.id = UUID()
            station.login_key = UUID()
            station.last_checking = 0
            const stationToCreate = new Station(station)
            console.log(stationToCreate.last_checkin)

            let newStationCreated = (await this._model.createStation(stationToCreate)).rows[0]
            return new Station(newStationCreated)
        } catch (error) {
            logger.error(error)
        }
    }
}
