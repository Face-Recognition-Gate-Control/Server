<template>
	<div>
		<button class="button is-inline is-primary" @click="doLogout">
			Logout
		</button>
	</div>
</template>
<script lang="ts">
import { defineComponent } from "vue";
// eslint-disable-next-line no-unused-vars
import { BlockedStatus } from "@/lib/BlockedStatus";
// eslint-disable-next-line no-unused-vars
import { UserEnterEvents } from "@/lib/UserEnterEvents";
import { logout } from "@/lib/helpers/auth";
import router from "@/router";

export default defineComponent({
	setup() {
		function doLogout() {
			logout();
			router.push("/login");
		}

		return {
			doLogout
		};
	}
});
</script>