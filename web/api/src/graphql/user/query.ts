import UserType from '@/graphql/user/type'
import { UserService } from '@/Service/UserService'
import { GraphQLFieldConfig, GraphQLInt, GraphQLList, GraphQLString } from 'graphql'

/**
 * QUERY contains all graphs which retrieves data.
 */

let userService: UserService

/**
 * Retrieves a single user by its id
 */
const User: GraphQLFieldConfig<any, any, { [id: string]: number }> = {
    type: UserType,
    args: {
        id: { type: GraphQLString },
    },
    resolve: async (root, args) => {
        return await userService.getUser(args.id)
    },
}

/**
 * Retrieves all users in the database
 */
const Users: GraphQLFieldConfig<any, any, { [id: string]: number }> = {
    type: new GraphQLList(UserType),
    resolve: async () => {
        return await userService.getAllUsers()
    },
}

export default (service: UserService) => {
    userService = service
    return { User, Users }
}
