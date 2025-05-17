import { jwtDecode } from "jwt-decode";

type DecodedJwt = {
    sub: string;
    roles: string[] | string;
};

export function getUserInfoFromToken(token: string) {
    try {
        const decoded = jwtDecode<DecodedJwt>(token);
        let roles: string[] = [];
        if (Array.isArray(decoded.roles)) {
            roles = decoded.roles;
        } else if (typeof decoded.roles === "string") {
            roles = decoded.roles.split(",").map(r => r.trim());
        }
        return {
            username: decoded.sub,
            roles,
        };
    } catch {
        return null;
    }
}
