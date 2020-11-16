import db from '@/loaders/postgres'
import { UserModel } from '@/Model/UserModel'
import { UserService } from '@/Service/UserService'
import { Authorizer } from './Authorizer'
import { createToken, verifyToken } from './jwt'

/**
 * Tries to validate the provided token.
 * It returns an Authorizer with the user owning the token attached or no user
 * attached if there was no token, invalid token or no user in the database.
 * @param bearerToken token to authenticate
 */
export async function UserRequestAuthentication(bearerToken: string | undefined) {
    const authorizer = new Authorizer()

    const token = verifyToken(bearerToken)
    if (token) {
        let userService = new UserService(new UserModel(db))
        const user = await userService.getUserWithRoles(token.userid as any)
        authorizer.user = user
    }
    return authorizer
}
