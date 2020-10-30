<template>
	<div class="container register">
		<div class="columns is-mobile">
			<div v-if="!registered" class="column">
				<h1 class="title">Register</h1>
				<p v-if="registrationFailed" class="is-size-5 has-text-danger">
					Something went wrong!
				</p>
				<form>
					<div class="field">
						<label class="label">First name</label>
						<div class="control">
							<input
								class="input"
								type="text"
								placeholder="Ole"
								v-model.trim="firstname"
							/>
						</div>
						<p class="help is-danger">{{ firstnameError }}</p>
					</div>

					<div class="field">
						<label class="label">Last name</label>
						<div class="control">
							<input
								class="input is-success"
								type="text"
								placeholder="Norman"
								v-model.trim="lastname"
							/>
						</div>
						<p class="help is-danger">{{ lastnameError }}</p>
					</div>

					<div class="field">
						<label class="label">Email</label>
						<div class="control">
							<input
								class="input is-success"
								type="email"
								placeholder="Email we can send notifications to"
								v-model.trim="email"
							/>
						</div>
						<p class="help is-danger">{{ emailError }}</p>
					</div>

					<div class="field">
						<label class="label">Phone number</label>
						<div class="control">
							<input
								class="input is-success"
								type="number"
								placeholder="Number we can send notifications to"
								v-model.number="phonenumber"
							/>
						</div>
						<p class="help is-danger">{{ phonenumberError }}</p>
					</div>

					<div class="field">
						<label class="label">Password</label>
						<div class="control">
							<input
								class="input is-success"
								type="password"
								placeholder="Password"
								v-model.trim="password"
							/>
						</div>
						<p class="help is-danger">{{ passwordError }}</p>
					</div>

					<div class="field">
						<label class="label">Retype password</label>
						<div class="control">
							<input
								class="input is-success"
								type="password"
								placeholder="Re-type the password"
								v-model.trim="repassword"
							/>
						</div>
						<p class="help is-danger">{{ repasswordError }}</p>
					</div>

					<div class="field is-grouped">
						<div class="control">
							<button class="button is-link" @click="registerUser">
								Register
							</button>
						</div>
						<div class="control">
							<button class="button is-link is-light">Cancel</button>
						</div>
					</div>
				</form>
			</div>
			<div v-if="registered" class="column">
				<h2 class="title">Registered, please go to the gate.</h2>
			</div>
		</div>
	</div>
</template>
<script lang="ts">
import { ref, defineComponent } from "vue";
import { gqlAxios } from "@/lib/helpers/axios";

import { isEmail, isEmpty, isEqual, range } from "@/lib/form/validators";
import { useRoute } from "vue-router";
// import router from "@/router";

export default defineComponent({
	setup() {
		const route = useRoute();
		const registered = ref(false);
		const registrationFailed = ref(false);

		const firstname = ref("");
		const lastname = ref("");
		const email = ref("");
		const password = ref("");
		const repassword = ref("");
		const phonenumber = ref();

		const firstnameError = ref("");
		const lastnameError = ref("");
		const emailError = ref("");
		const passwordError = ref("");
		const repasswordError = ref("");
		const phonenumberError = ref("");

		const validate = () => {
			if (isEmpty(firstname.value)) {
				firstnameError.value = "Required";
			} else {
				firstnameError.value = "";
			}

			if (isEmpty(lastname.value)) {
				lastnameError.value = "Required";
			} else {
				lastnameError.value = "";
			}

			if (!isEmail(email.value)) {
				emailError.value = "Please provide a valid email";
			} else {
				emailError.value = "";
			}

			if (!range(password.value, 6)) {
				passwordError.value = "Password must be atleast 6 character";
			} else {
				passwordError.value = "";
			}

			if (!isEqual(password.value, repassword.value)) {
				repasswordError.value = "Passwords are not identical";
			} else {
				repasswordError.value = "";
			}

			if (isEmpty(phonenumber.value)) {
				phonenumberError.value = "Phone number is required";
			} else if (!range(phonenumber.value.toString(), 8)) {
				phonenumberError.value = "Phonenumber must be 8 characters";
			} else {
				phonenumberError.value = "";
			}

			return !firstnameError.value &&
				!lastnameError.value &&
				!emailError.value &&
				!passwordError.value &&
				!repasswordError.value &&
				!phonenumberError.value
				? true
				: false;
		};

		const registerUser = async () => {
			let valid = validate();
			if (valid) {
				let data = {
					query: `
					mutation{
  User(registration_token:"${route.params.token}",firstname:"${firstname.value}",lastname:"${lastname.value}",email:"${email.value}",telephone:${phonenumber.value},password:"${password.value}"){
	  id
  }
}`,
				};
				try {
					const res = await gqlAxios.post("", data);
					if (res.data.data.User) {
						registered.value = true;
					} else {
						registrationFailed.value = true;
					}
				} catch (error) {
					console.log(error);
				}
			}
		};

		return {
			registrationFailed,
			registered,
			firstname,
			lastname,
			email,
			password,
			repassword,
			phonenumber,
			registerUser,
			firstnameError,
			lastnameError,
			emailError,
			passwordError,
			repasswordError,
			phonenumberError,
		};
	},
});
</script>