import { GraphQLObjectType, GraphQLString, GraphQLUnionType } from 'graphql'
import { UserType } from '../user/type'

let AuthenticationType = new GraphQLObjectType({
    name: 'AuthenticationType',
    fields: () => ({
        user: { type: UserType },
    }),
    description: 'Authentication',
})

export { AuthenticationType }
