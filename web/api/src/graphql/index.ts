import user from '@/graphql/user'
import station from '@/graphql/station'
import role from '@/graphql/role'

import { GraphQLObjectType, GraphQLSchema } from 'graphql'

/**
 * Query graph
 */
const Query = new GraphQLObjectType({
    name: 'Query',
    fields: () => ({ ...user.query, ...station.query, ...role.query }),
})
/**
 * Mutation graph
 */
const Mutation = new GraphQLObjectType({
    name: 'Mutation',
    fields: () => ({ ...user.mutation, ...station.mutation, ...role.mutation }),
})

/**
 * Create the complete schema for Graphql
 */
const schema = new GraphQLSchema({ query: Query, mutation: Mutation })

export function generateSchema() {
    return schema
}
