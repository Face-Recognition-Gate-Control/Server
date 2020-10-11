import { GraphQLInt, GraphQLObjectType, GraphQLString } from 'graphql'

let GateType = new GraphQLObjectType({
    name: 'GateType',
    fields: () => ({
        id: { type: GraphQLString },
        login_key: { type: GraphQLString },
        station_name: { type: GraphQLString },
        last_checkin: { type: GraphQLString },
    }),
    description: 'Gate station',
})

export default GateType
