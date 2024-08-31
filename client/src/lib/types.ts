import { z } from "zod";

export const PlayerSchema =
    z.object({
        id: z.number(),
        name: z.string(),
        status: z.string(),
        joinDate: z.string(),
        purgeDate: z.string(),
        avatar: z.string()
    })

export const PlayerListSchema = z.array(PlayerSchema).nullable()

export type Player = z.infer<typeof PlayerSchema>;

export const ServerSchema = z.object({
    id: z.number(),
    name: z.string(),
    status: z.string(),
    apiToken: z.string(),
});

export const ServerListSchema = z.array(ServerSchema).nullable()

export type Server = z.infer<typeof ServerSchema>;

export const ApplicationSchema = z.object({
    id: z.number(),
    name: z.string(),
    status: z.string(),
    avatar: z.string(),
});

export const ApplicationListSchema = z.array(ApplicationSchema).nullable()


export type Application = z.infer<typeof ApplicationSchema>;


