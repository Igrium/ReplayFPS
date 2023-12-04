# Replay FPS

*Which should really be called "Replay FPV" but I was thinking of first-person shooters at the time*

## About

The [Replay Mod](https://www.replaymod.com/) is a popular modification for Minecraft: Java Edition which allows you to capture gameplay for subsequent playback. While the Replay Mod is very powerful, it's severely lacking when it comes to first-person gameplay.

By default, when you render a replay from the first-person perspective of a player, the view appears extremely sluggish. This is due to the fact that Minecraft only properly updates player positions 10 times per second, so the game must interpolate the missing data.

This addon aims to fix this.

By storing client-side camera data alongside the primary replay data, the movements of the local client can be captured in greater detail, leading to a much better first-person playback experience.

## Usage

This addon doesn't contain any user-facing usage beyond the normal Replay Mod stuff. Simply install it alongside the replay mod and use it as normal. Keep in mind, however, that the enhancements provided by this addon will only ever apply to the player that recorded the replay, and only if that player had this addon installed while recording.

## Technical

### Client-Capture

The reason that first-person movement looks so bad normally is that the Replay Mod only records the game packets communicated between the server and client. In most cases, this looks identical to observing the game in real-time. However, when it comes to first-person views, the server doesn't send enough data to accurately recreate the original movement. In fact, this is why spectating another player in Vanilla results in the same type of slushiness.

The solution is store an additional stream of data alongside the packet stream. This stream is comprised of a series of channels, each holding a continuous value at a constant sample rate. These channels are written to directly by the client at capture time, bypassing the server-client packet system entirely. Not only does this allow for samples to be captured at a much higher rate than would be possible with packets, but it also enables data to be captured that isn't normally synced with the server in the first place.

In order to store this sizeable data in a realistic way, a custom binary format was developed. When a replay is captured using this addon, a "client-capture" (`ccap`) file is inserted into the replay archive alongside the packet data. The specification of this file can be found in this repo.

### Packets

Some data, however, doesn't make sense to store in a continuous stream. Discrete forms of data, as changes to the hotbar contents, are stored as custom packets that are injected into the replay packet stream. These so called "fake packets" get serialized to a `PacketByteBuf` at recording time, but instead of being sent to the server, they get saved into the replay file locally. During playback, the Replay Mod then serves these fake packets back to Minecraft as a normal packet, where a custom handler can parse and apply them to the world.

In addition to fake packets, "packet redirectors" can be registered to override how a vanilla packet gets applied during playback. This allows numerous packets, which are normally unusable during a replay, to be parsed and applied towards reconstructing the original client experience.

## Building

Like most mods, to build this project, simply open a console to the root directory and enter `./gradlew build`. However, it's possible that the build will fail due to a missing dependency called 'CraftFX'. This is a dependency of the `ccap_viewer` subproject which, as of now, is built alongside the main project. To fix this, clone and build [CraftFX](https://github.com/Igrium/CraftFX), and publish it to Maven Local.


