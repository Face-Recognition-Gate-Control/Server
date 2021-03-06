import query from '@/graphql/user/query'
import mutation from '@/graphql/user/mutation'
import { UserModel } from '@/Model/UserModel'
import { UserService } from '@/Service/UserService'
import db from '@/loaders/postgres'

const userService = new UserService(new UserModel(db))

/**
 * Initializes the query/mutation graphs, and injects the service for
 * handling all requests to the graphs
 */
export default {
    query: query(userService),
    mutation: mutation(userService),
}
