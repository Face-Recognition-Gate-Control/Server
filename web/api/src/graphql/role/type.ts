import { GraphQLObjectType, GraphQLString } from 'graphql'

/**
 * Describes a role
 */
let RoleType = new GraphQLObjectType({
    name: 'RoleType',
    fields: () => ({
        role_name: { type: GraphQLString },
    }),
    description: 'A single role',
})

export default RoleType
