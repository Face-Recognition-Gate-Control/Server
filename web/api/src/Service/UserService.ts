import { User } from '@/lib/user/User'
import { UserModel } from '@/Model/UserModel'

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
        return (await this._model.getUserById(id)).rows[0]
    }

    /**
     * Returns all users or null of no users are found
     */
    async getAllUsers() {
        return (await this._model.getAllUsers()).rows
    }

    async createUser(user: User) {}
}
