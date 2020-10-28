import StationType from '@/graphql/station/type'
import { GraphQLFieldConfig, GraphQLList, GraphQLString } from 'graphql'
import { StationService } from '@/Service/StationService'
import { RequestContext } from '@/loaders/express'
import { Roles } from '@/lib/auth/roles'

/**
 * QUERY contains all graphs which retrieves data.
 */

var stationService: StationService

/**
 * Retrieves a single gate station by its id
 */
var Station: GraphQLFieldConfig<any, RequestContext, { [id: string]: string }> = {
    type: StationType,
    args: {
        id: { type: GraphQLString },
    },
    resolve: async (root, args: { [id: string]: string }, ctx) => {
        if (!ctx.authorizer.hasRole([Roles.Admin, Roles.Moderator])) return
        return await stationService.getStationById(args.id)
    },
}

/**
 * Retrieves all gate stations in the database
 */
var Stations: GraphQLFieldConfig<any, RequestContext, { [id: string]: string }> = {
    type: new GraphQLList(StationType),
    resolve: async (root, args, ctx) => {
        if (!ctx.authorizer.hasRole([Roles.Admin, Roles.Moderator])) return
        return await stationService.getAllStations()
    },
}

export default (service: StationService) => {
    stationService = service
    return { Station, Stations }
}
