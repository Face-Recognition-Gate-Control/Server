import jwt from 'jsonwebtoken'
import config from '@/config'
import { AuthTokenData } from './AuthTokenData'

export { verifyToken, createToken }

/**
 * Verifies a an authorization token, and returns the token if successfull, else undefined
 * @param bearerToken the authorization token to verify: Bearer <TOKEN>
 */
function verifyToken(bearerToken: string | undefined) {
    let authorizedToken: AuthTokenData | undefined
    if (bearerToken) {
        const token = bearerToken.split(' ')[1]
        jwt.verify(token, config.jwt_secret!, (e, tokenData) => {
            if (tokenData) authorizedToken = tokenData as AuthTokenData
        })
    }
    return authorizedToken
}

/**
 * Generates a new token, with custom data attached.
 * @param data data to pass into the token
 */
function createToken(data: AuthTokenData) {
    return jwt.sign(data, config.jwt_secret!)
}
