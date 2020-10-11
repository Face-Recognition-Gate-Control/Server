import UserType from '@/graphql/user/type'
import { GraphQLFieldConfig, GraphQLInt, GraphQLList, GraphQLNonNull, GraphQLString } from 'graphql'
import database from '@/loaders/postgres'
import { UserService } from '@/Service/UserService'

let userSerivce: UserService

type NewUser = {
    fistname: string
    lastname: string
    email: string
    telephone: number
    password: string
}

var User: GraphQLFieldConfig<any, any, any> = {
    type: UserType,
    args: {
        fistname: { type: new GraphQLNonNull(GraphQLString) },
        lastname: { type: new GraphQLNonNull(GraphQLString) },
        email: { type: new GraphQLNonNull(GraphQLString) },
        telephone: { type: new GraphQLNonNull(GraphQLInt) },
        password: { type: new GraphQLNonNull(GraphQLString) },
    },
    resolve: async (root: any, args: NewUser, context: any) => {
        // TODO : VALIDATION, HASH PASSWORD
        return (
            await database.query(
                'INSERT INTO users (firstname, lastname, email, telephone, password) VALUES ($1, $2,$3,$4,$5) RETURNING *',
                [args.fistname, args.lastname, args.email, args.telephone, args.password]
            )
        ).rows[0]
    },
}

export default (service: UserService) => {
    userSerivce = service
    return { User }
}
