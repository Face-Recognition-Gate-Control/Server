import { User, UserType } from '@/lib/user/User'
import { UserModel } from '@/Model/UserModel'
import logger from '@/loaders/logger'

export class UserService {
    private _model: UserModel

    constructor(model: UserModel) {
        this._model = model
    }
    /**
     * Returns a user, or null if a user is not found
     * @param id id of the user to get
     */
    async getUser(id: number) {
        try {
            return (await this._model.getUserById(id)).rows[0]
        } catch (error) {
            logger.error(error)
        }
    }

    /**
     * Returns all users or null of no users are found
     */
    async getAllUsers() {
        try {
            return (await this._model.getAllUsers()).rows
        } catch (error) {
            logger.error(error)
        }
    }

    /**
     * Creates a new user
     * @param user user fields
     */
    async createUser(user: UserType) {
        // TODO : VALIDATION, HASH PASSWORD
        try {
            const newUser = new User(user)
            return (await this._model.createUser(newUser)).rows[0]
        } catch (error) {
            logger.error(error)
        }
    }
}
