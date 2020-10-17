import { GraphQLInt, GraphQLObjectType, GraphQLString } from 'graphql'
import { UserRoles } from './query'

/**
 * Describes the user type for GraphQL.
 * This is required for GraphQL to understand how a user "looks like" and what
 * types each field has.
 */
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
        roles: UserRoles,
    }),
    description: 'A registered user',
})

export default UserType
