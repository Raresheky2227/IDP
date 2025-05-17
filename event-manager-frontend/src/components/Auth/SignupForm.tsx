import React, { useState } from "react";
import { signup } from "../../api/auth";
import { useNavigate } from "react-router-dom";

const SignupForm: React.FC = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [role, setRole] = useState("ROLE_USER");
    const [message, setMessage] = useState<string | null>(null);
    const [error, setError] = useState<string | null>(null);
    const navigate = useNavigate();

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();
        setError(null);
        setMessage(null);
        try {
            await signup(username, password, role);
            setMessage("Signup successful. Please login.");
            setTimeout(() => navigate("/login"), 1000);
        } catch (err: any) {
            setError("Signup failed: " + (err.response?.data?.message || "Unknown error"));
        }
    }

    return (
        <form onSubmit={handleSubmit}>
            <h2>Sign Up</h2>
            <label>
                Username
                <input value={username} onChange={e => setUsername(e.target.value)} required />
            </label>
            <label>
                Password
                <input type="password" value={password} onChange={e => setPassword(e.target.value)} required />
            </label>
            <label>
                Role
                <select value={role} onChange={e => setRole(e.target.value)}>
                    <option value="ROLE_USER">USER</option>
                    <option value="ROLE_VIP">VIP</option>
                </select>
            </label>
            <button type="submit">Sign Up</button>
            {error && <div className="error">{error}</div>}
            {message && <div className="success">{message}</div>}
        </form>
    );
};

export default SignupForm;
