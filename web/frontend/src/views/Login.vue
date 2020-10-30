<template>
	<div class="container register">
		<div class="columns is-mobile">
			<div class="column">
				<h1 class="title">Login</h1>
				<p v-if="loginFailed" class="is-size-5 has-text-danger">
					Could not login!
				</p>
				<form v-on:submit.prevent>
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

					<div class="field is-grouped">
						<div class="control">
							<button class="button is-link" @click="login">Login</button>
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
</template>
<script lang="ts">
import { ref, defineComponent } from "vue";
import { gqlAxios } from "@/lib/helpers/axios";
import { setToken } from "@/lib/helpers/auth";

import { isEmpty } from "@/lib/form/validators";
// import { useRoute } from "vue-router";
import router from "@/router";

export default defineComponent({
	setup() {
		// const route = useRoute();
		// const registered = ref(false);
		const loginFailed = ref(false);

		const email = ref("");
		const password = ref("");

		const emailError = ref("");
		const passwordError = ref("");

		const validate = () => {
			if (isEmpty(email.value)) {
				emailError.value = "Required";
			} else {
				emailError.value = "";
			}

			if (isEmpty(password.value)) {
				passwordError.value = "Required";
			} else {
				passwordError.value = "";
			}

			return true;
		};

		const login = async () => {
			let valid = validate();
			if (valid) {
				let data = {
					query: `
mutation{
  Auth(email:"${email.value}", password:"${password.value}"){
    user{
      id
	}
	token
  }
}`,
				};
				try {
					const res = await gqlAxios.post("", data);
					console.log(res);
					const auth = res.data.data.Auth;

					if (auth && auth.token) {
						setToken(auth.token);
						router.push("user");
					} else {
						loginFailed.value = true;
					}
				} catch (error) {
					loginFailed.value = true;
				}
			}
		};

		return {
			loginFailed,
			email,
			password,
			emailError,
			passwordError,
			login,
		};
	},
});
</script>