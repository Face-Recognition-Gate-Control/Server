import { GraphQLFieldConfig, GraphQLInt, GraphQLNonNull, GraphQLString } from 'graphql'
import { StationService } from '@/Service/StationService'
import { StationType as NewStation } from '@/lib/station/Station'
import StationType from './type'

/**
 * MUTATION is for updating/creating new data entries
 */

let stationService: StationService

/**
 * Creates a new station
 */
var Station: GraphQLFieldConfig<any, any, any> = {
    type: StationType,
    args: {
        station_name: { type: new GraphQLNonNull(GraphQLString) },
    },
    resolve: async (root: any, args: NewStation, context: any) => {
        return await stationService.createStation({
            station_name: args.station_name,
        })
    },
}

export default (service: StationService) => {
    stationService = service
    return { Station }
}
