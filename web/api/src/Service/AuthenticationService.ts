import { User, UserType } from '@/lib/user/User'
import { UserModel } from '@/Model/UserModel'
import logger from '@/loaders/logger'
import { compare, hash } from '@/lib/passwordHasher'
import { createToken } from '@/lib/auth/jwt'

export class AuthenticationService {
    private _model: UserModel

    constructor(model: UserModel) {
        this._model = model
    }
    /**
     * Returns a user, or null if a user is not found
     * @param id id of the user to get
     */
    async authenticateUser({ email, password }: { email: string; password: string }) {
        try {
            email = email.toLowerCase()
            const user = (await this._model.getUserByMail(email)).rows[0]
            if (!user) return
            let isCorrectPassword = await compare(password, user?.password)
            if (isCorrectPassword) {
                return {
                    user: new User(user),
                    token: createToken({ userid: user.id?.toString()! }),
                }
            }
        } catch (error) {
            logger.error(error)
        }
    }
}
