import axios from "axios";

const API = process.env.REACT_APP_API_URL + "/api/auth";

export async function signup(username: string, password: string, role: string) {
    const res = await axios.post(`${API}/signup`, {
        username, password, roles: role,
    });
    return res.data;
}

export async function login(username: string, password: string) {
    const res = await axios.post(`${API}/login`, { username, password });
    return res.data.token;
}
