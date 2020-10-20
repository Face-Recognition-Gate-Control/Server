import bcrypt from 'bcrypt'

/**
 * How many rounds it should do to generate a random salt
 * Higher number increase computation time
 */
const saltRounds = 10

/**
 * Hashes a given input and returns the hash
 * @param input data to hash
 */
export const hash = async (input: any) => {
    return await bcrypt.hash(input, saltRounds)
}

/**
 * Compares a raw value agains a hash to check if the
 * hashed value is equal to the raw value.
 * Returns true if match, else false.
 * @param raw raw data to compare
 * @param hashed hashed value to compare against
 */
export const compare = async (raw: any, hashed: string) => {
    return await bcrypt.compare(raw, hashed)
}
