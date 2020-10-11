import UserType from '@/graphql/user/type'
import { UserService } from '@/Service/UserService'
import { GraphQLFieldConfig, GraphQLInt, GraphQLList, GraphQLString } from 'graphql'

let userService: UserService
const User: GraphQLFieldConfig<any, any, { [id: string]: number }> = {
    type: UserType,
    args: {
        id: { type: GraphQLString },
    },
    resolve: async (root, args) => {
        return await userService.getUser(args.id)
    },
}

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
