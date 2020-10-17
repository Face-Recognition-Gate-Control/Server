import { RoleModel } from '@/Model/RoleModel'
import logger from '@/loaders/logger'
import { print } from 'graphql'

export class RoleService {
    private _model: RoleModel

    constructor(model: RoleModel) {
        this._model = model
    }
    /**
     * Returns a role, or null if a role is not found
     * @param name name of the role to get
     */
    async getRole(name: string) {
        try {
            return (await this._model.getRole(name)).rows[0]
        } catch (error) {
            logger.error(error)
        }
    }

    /**
     * Returns all roles or null if no roles are found
     */
    async getAllRoles() {
        try {
            const res = (await this._model.getAllRoles()).rows
            console.log(res)

            return res
        } catch (error) {
            logger.error(error)
        }
    }

    /**
     * Creates a new role
     * @param name name of the role
     */
    async addNewRole(name: string) {
        try {
            return (await this._model.addNewRole(name)).rows[0]
        } catch (error) {
            logger.error(error)
        }
    }
}
