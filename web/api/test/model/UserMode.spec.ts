import { NewUser } from '@/lib/user/User'
import assert from 'assert'
import { Client } from 'pg'
import { UserModel } from '../../src/Model/UserModel'

import db from './db'

describe('User Model', () => {
    let model: UserModel
    let station_id = 'f6f210bc-c9ad-430c-a0db-60afb07e1310'
    let reg_token = 'f6f210bc-c9ad-430c-a0db-60afb07e1319'
    let validUser: NewUser = {
        telephone: 12345678,
        registration_token: reg_token,
        password: '12345678',
        lastname: 'lastname',
        firstname: 'firstname',
        email: 'test@live.com',
    }
    let userid = -1

    before(async () => {
        await db.connect()
        model = new UserModel(db)

        await db.query(`INSERT INTO stations (id, login_key, station_name, last_chekin) VALUES
        (
            '${station_id}',
            'secret',
            'station1',
            1000
        )`)

        await db.query(`INSERT INTO new_user_queue (tmp_id, face_vec, station_id, file_name, added_ts) VALUES
        (
            '${reg_token}',
            '{1,2,3}',
            '${station_id}',
            'test.png',
            1000
        )`)
    })

    after(async () => {
        await db.query(`DELETE FROM new_user_queue WHERE tmp_id = '${reg_token}'`)
        await db.query(`DELETE FROM stations WHERE id = '${station_id}'`)
        await db.query(`DELETE FROM login_referance WHERE user_id = '${userid}'`)
        await db.query(`DELETE FROM users WHERE id = '${userid}'`)
    })

    it('should return true if token exist', async function() {
        let res = await model.getUserRegistrationData(reg_token)
        assert.strictEqual(1, res.rowCount)
    })

    it('should return registration data', async function() {
        let res = (await model.getUserRegistrationData(reg_token)).rows[0]
        assert.strictEqual(reg_token, res.tmp_id)
    })

    it('should not return registration data', async function() {
        let res = (await model.getUserRegistrationData('f6f210bc-c9ad-430c-a0db-60afb07e1877'))
            .rows[0]
        assert.strictEqual(undefined, res)
    })

    it('should create a user and add login referance', async function() {
        let user = (await model.createUser(validUser))?.rows[0]
        userid = user.id
        assert.strictEqual(undefined, undefined)
    })
})
