import { GraphQLInt, GraphQLObjectType, GraphQLString } from 'graphql'

/**
 * Describes the station type for GraphQL.
 * This is required for GraphQL to understand how a station "looks like" and what
 * types each field has.
 */
let StationType = new GraphQLObjectType({
    name: 'StationType',
    fields: () => ({
        id: { type: GraphQLString },
        login_key: { type: GraphQLString },
        station_name: { type: GraphQLString },
        last_checkin: { type: GraphQLString },
    }),
    description: 'Gate station',
})

export default StationType
