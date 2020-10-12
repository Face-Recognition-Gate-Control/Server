import query from '@/graphql/station/query'
import { StationModel } from '@/Model/StationModel'
import { StationService } from '@/Service/StationService'

const stationService = new StationService(new StationModel())

export default {
    query: query(stationService),
}
