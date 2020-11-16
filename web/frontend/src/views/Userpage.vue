<template>
	<div class="container">
		<h1 class="title">Userpage</h1>

		<div class="columns is-desktop py-5">
			<div class="column">
				<h2 class="subtitle has-text-weight-bold">Status</h2>

				<button class="button is-danger" v-if="!isBlocked" @click="setBlocked">
					I've got Corona
				</button>
				<div v-if="isBlocked && isBlockedStatus">
					Access is revoked because of: <i> {{ isBlockedStatus.reason }} </i
					><br />
					<b>Reported at:</b>
					{{ getDateString(getEpochDate(isBlockedStatus.timeOfBlock)) }}
					<br /><b>Will last until:</b>
					{{
						getDateString(
							addDaysToDate(getEpochDate(isBlockedStatus.timeOfBlock), 14)
						)
					}}
				</div>
			</div>
			<div class="column">
				<h2 class="subtitle has-text-weight-bold">Logs</h2>
				<table class="table">
					<thead>
						<tr>
							<th>Room</th>
							<th>Date</th>
							<th>Time</th>
						</tr>
					</thead>
					<tbody>
						<tr
							class=""
							v-for="enterEvent in userEnterEvents"
							v-bind:key="enterEvent.enter_time"
						>
							<td>{{ enterEvent.station_name }}</td>
							<td>{{ getDateString(getEpochDate(enterEvent.enter_time)) }}</td>
							<td>
								{{ getHourMinuteFromDate(getEpochDate(enterEvent.enter_time)) }}
							</td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
		<logout />
	</div>
</template>
<script lang="ts">
import { defineComponent, reactive, ref } from "vue";
import { gqlAxios } from "@/lib/helpers/axios";
// eslint-disable-next-line no-unused-vars
import { BlockedStatus } from "@/lib/BlockedStatus";
// eslint-disable-next-line no-unused-vars
import { UserEnterEvents } from "@/lib/UserEnterEvents";
import Logout from "@/components/Logout.vue";

export default defineComponent({
	components: {
		Logout
	},
	setup() {
		let blockedStatus: BlockedStatus = reactive({
			reason: "",
			timeOfBlock: 0
		});

		let userEnterEvents: UserEnterEvents[] = reactive([]);

		const isBlocked = ref(true);

		console.log(gqlAxios.defaults.headers);

		async function getBlocked() {
			let data: any = {
				query: `
query{
    UserBlocked{
      time_of_block
         reason
    }
}
`
			};
			try {
				const res = await gqlAxios.post("", data);
				if (res.data.data) {
					let response = res.data.data.UserBlocked;
					if (response) {
						blockedStatus.reason = response.reason;
						blockedStatus.timeOfBlock = Number.parseInt(response.time_of_block);
						isBlocked.value = true;
					} else {
						isBlocked.value = false;
					}
				}
			} catch (error) {
				console.log(error);
			}
		}

		async function setBlocked() {
			let data: any = {
				query: `
mutation{
  SetUserBlock{
    time_of_block
    reason
  }
}
`
			};
			try {
				const res = await gqlAxios.post("", data);
				if (res.data.data) {
					let response = res.data.data.SetUserBlock;
					console.log(response);

					if (response) {
						blockedStatus.reason = response.reason;
						blockedStatus.timeOfBlock = Number.parseInt(response.time_of_block);
						isBlocked.value = true;
					}
				}
			} catch (error) {
				console.log(error);
			}
		}

		async function getEnterEvents() {
			let data: any = {
				query: `
query{
    UserEnterEvents{
      enter_time
      station_name
    }
}
`
			};
			try {
				const res = await gqlAxios.post("", data);
				if (res.data.data) {
					let response = res.data.data.UserEnterEvents;

					if (response) {
						(response as Array<UserEnterEvents>)
							.sort((e, k) => k.enter_time - e.enter_time)
							.forEach(e => {
								userEnterEvents.push({
									station_name: e.station_name,
									enter_time: Number.parseInt(e.enter_time.toString())
								});
							});
					}
				}
			} catch (error) {
				console.log(error);
			}
		}

		getBlocked();
		getEnterEvents();

		function getDateString(date: Date) {
			return `${date.getUTCDate()}/${date.getUTCMonth() +
				1}/${date.getFullYear()}`;
		}

		function getHourMinuteFromDate(date: Date) {
			return `${date.getUTCHours()}:${date.getUTCMinutes()}`;
		}

		function addDaysToDate(date: Date, days: number) {
			date.setDate(date.getDate() + days);
			return date;
		}

		function getEpochDate(epoch: number) {
			return new Date(epoch * 1000);
		}

		return {
			setBlocked,
			isBlocked,
			isBlockedStatus: blockedStatus,
			getDateString,
			addDaysToDate,
			userEnterEvents,
			getHourMinuteFromDate,
			getEpochDate
		};
	}
});
</script>