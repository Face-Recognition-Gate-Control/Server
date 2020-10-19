import mutation from '@/graphql/authentication/mutation'
import { UserModel } from '@/Model/UserModel'
import { AuthenticationService } from '@/Service/AuthenticationService'

const authService = new AuthenticationService(new UserModel())

/**
 * Initializes the query/mutation graphs, and injects the service for
 * handling all requests to the graphs
 */
export default {
    mutation: mutation(authService),
}
