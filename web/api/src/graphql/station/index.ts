import query from '@/graphql/station/query'
import mutation from '@/graphql/station/mutation'
import { StationModel } from '@/Model/StationModel'
import { StationService } from '@/Service/StationService'
import db from '@/loaders/postgres'

const stationService = new StationService(new StationModel(db))

/**
 * Initializes the query/mutation graphs, and injects the service for
 * handling all requests to the graphs
 */
export default {
    query: query(stationService),
    mutation: mutation(stationService),
}
