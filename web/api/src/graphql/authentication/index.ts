import mutation from '@/graphql/authentication/mutation'
import { UserModel } from '@/Model/UserModel'
import { AuthenticationService } from '@/Service/AuthenticationService'
import db from '@/loaders/postgres'

const authService = new AuthenticationService(new UserModel(db))

/**
 * Initializes the query/mutation graphs, and injects the service for
 * handling all requests to the graphs
 */
export default {
    mutation: mutation(authService),
}
