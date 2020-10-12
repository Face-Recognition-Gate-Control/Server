import UserType from '@/graphql/user/type'
import { GraphQLFieldConfig, GraphQLInt, GraphQLNonNull, GraphQLString } from 'graphql'
import { UserService } from '@/Service/UserService'
import { UserType as NewUserType } from '@/lib/user/User'

let userSerivce: UserService

var User: GraphQLFieldConfig<any, any, any> = {
    type: UserType,
    args: {
        firstname: { type: new GraphQLNonNull(GraphQLString) },
        lastname: { type: new GraphQLNonNull(GraphQLString) },
        email: { type: new GraphQLNonNull(GraphQLString) },
        telephone: { type: new GraphQLNonNull(GraphQLInt) },
        password: { type: new GraphQLNonNull(GraphQLString) },
    },
    resolve: async (root: any, args: NewUserType, context: any) => {
        return await userSerivce.createUser({
            firstname: args.firstname,
            lastname: args.lastname,
            email: args.email,
            telephone: args.telephone,
            password: args.password,
        })
    },
}

export default (service: UserService) => {
    userSerivce = service
    return { User }
}
