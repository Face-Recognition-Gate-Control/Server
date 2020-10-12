import StationType from '@/graphql/station/type'
import { GraphQLFieldConfig, GraphQLInt, GraphQLList, GraphQLString } from 'graphql'
import database from '@/loaders/postgres'
import { StationService } from '@/Service/StationService'

var stationService: StationService

var Station: GraphQLFieldConfig<any, any, { [id: string]: string }> = {
    type: StationType,
    args: {
        id: { type: GraphQLString },
    },
    resolve: async (root, args: { [id: string]: string }, context) => {
        return await stationService.getStationById(args.id)
    },
}

var Stations: GraphQLFieldConfig<any, any, { [id: string]: string }> = {
    type: new GraphQLList(StationType),
    resolve: async () => {
        return await stationService.getAllStations()
    },
}

export default (service: StationService) => {
    stationService = service
    return { Station, Stations }
}
