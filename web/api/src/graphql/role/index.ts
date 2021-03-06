import query from '@/graphql/role/query'
import mutation from '@/graphql/role/mutation'
import { RoleModel } from '@/Model/RoleModel'
import { RoleService } from '@/Service/RoleService'
import db from '@/loaders/postgres'

const roleService = new RoleService(new RoleModel(db))

/**
 * Initializes the query/mutation graphs, and injects the service for
 * handling all requests to the graphs
 */
export default {
    query: query(roleService),
    mutation: mutation(roleService),
}
