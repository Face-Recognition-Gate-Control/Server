import { NewUser, User, UserType } from '@/lib/user/User'
import { UserModel } from '@/Model/UserModel'
import logger from '@/loaders/logger'
import { hash } from '@/lib/passwordHasher'
import validator from 'validator'

export class UserService {
    private _model: UserModel

    constructor(model: UserModel) {
        this._model = model
    }
    /**
     * Returns a user, or null if a user is not found
     * @param id id of the user to get
     */
    async getUser(id: string) {
        try {
            return new User((await this._model.getUserById(id)).rows[0])
        } catch (error) {
            logger.error(error)
        }
    }

    /**
     * Returns a user with all roles, or null if a user is not found
     * @param id id of the user to get
     */
    async getUserWithRoles(id: string) {
        try {
            return new User((await this._model.getUserWithRoles(id)).rows[0])
        } catch (error) {
            logger.error(error)
        }
    }

    /**
     * Returns all users or null of no users are found
     */
    async getAllUsers() {
        try {
            return (await this._model.getAllUsers()).rows.map((user) => new User(user))
        } catch (error) {
            logger.error(error)
        }
    }

    /**
     * Returns all roles for the user
     */
    async getUserRoles(id: string) {
        try {
            return (await this._model.getRolesForUser(id)).rows
        } catch (error) {
            logger.error(error)
        }
    }
    /**
     * Returns all entering events for the user
     */
    async getUserEnterEvents(id: string) {
        try {
            return (await this._model.getUserEnterEvents(id)).rows
        } catch (error) {
            logger.error(error)
        }
    }

    /**
     * Creates a new user
     * @param user user fields
     */
    async createUser(user: NewUser) {
        try {
            if (this.validateUser(user)) {
                user.email = user.email.toLowerCase()
                user.password = await hash(user.password)
                let newUserCreated = await this._model.createUser(user)
                if (newUserCreated) {
                    return new User(newUserCreated.rows[0])
                }
            }
        } catch (error) {
            logger.error(error)
        }
    }

    /**
     * Validates a user for creation
     * @param user user to validate
     */
    private validateUser(user: UserType) {
        let validators = [
            validator.isAlpha(user.firstname!),
            validator.isAlpha(user.lastname!),
            validator.isEmail(user.email!),
            validator.isNumeric(user.telephone?.toString()!),
            validator.isLength(user.telephone?.toString()!, {
                min: 8,
                max: 8,
            }),
            validator.isLength(user.password!, {
                min: 6,
            }),
        ]

        return validators.every((v) => {
            return v == true
        })
    }
}
