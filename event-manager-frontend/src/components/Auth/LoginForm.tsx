import React, { useState } from "react";
import { useAuthContext } from "../../context/AuthContext";
import { useNavigate, useLocation } from "react-router-dom";
import { login } from "../../api/auth";

const LoginForm: React.FC = () => {
    const { login: doLogin } = useAuthContext();
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState<string | null>(null);
    const navigate = useNavigate();
    const location = useLocation();
    const from = (location.state as any)?.from?.pathname || "/";

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();
        setError(null);
        try {
            const token = await login(username, password);
            doLogin(token);
            navigate(from, { replace: true });
        } catch (err: any) {
            setError("Invalid credentials");
        }
    }

    return (
        <form onSubmit={handleSubmit}>
            <h2>Login</h2>
            <label>
                Username
                <input value={username} onChange={e => setUsername(e.target.value)} required />
            </label>
            <label>
                Password
                <input type="password" value={password} onChange={e => setPassword(e.target.value)} required />
            </label>
            <button type="submit">Login</button>
            {error && <div className="error">{error}</div>}
        </form>
    );
};

export default LoginForm;
