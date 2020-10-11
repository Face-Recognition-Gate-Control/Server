import { GraphQLInt, GraphQLObjectType, GraphQLString } from 'graphql'

let UserType = new GraphQLObjectType({
    name: 'UserType',
    fields: () => ({
        id: { type: GraphQLInt },
        firstname: { type: GraphQLString },
        lastname: { type: GraphQLString },
        email: { type: GraphQLString },
        telephone: { type: GraphQLInt },
        password: { type: GraphQLString },
        created: { type: GraphQLString },
    }),
    description: 'A registered user',
})

export default UserType
