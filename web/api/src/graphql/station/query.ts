import StationType from '@/graphql/station/type'
import { GraphQLFieldConfig, GraphQLInt, GraphQLList, GraphQLString } from 'graphql'
import database from '@/loaders/postgres'
var Station: GraphQLFieldConfig<any, any, { [id: string]: string }> = {
    type: StationType,
    args: {
        id: { type: GraphQLString },
    },
    resolve: async (root, args: { [id: string]: string }, context) => {
        return (await database.query('SELECT * FROM stations WHERE id = $1', [args.id])).rows[0]
    },
}

var Stations: GraphQLFieldConfig<any, any, { [id: string]: string }> = {
    type: StationType,
    resolve: async () => {
        return (await database.query('SELECT * FROM stations')).rows
    },
}

export default {
    Station,
    Stations,
}
