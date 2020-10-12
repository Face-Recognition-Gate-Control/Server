import { StationModel } from '@/Model/StationModel'
import logger from '@/loaders/logger'

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
}
