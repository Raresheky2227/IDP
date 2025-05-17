import React from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuthContext } from "../../context/AuthContext";

const Navbar: React.FC = () => {
    const { user, logout } = useAuthContext();
    const navigate = useNavigate();

    function handleLogout() {
        logout();
        navigate("/login");
    }

    return (
        <nav
            style={{
                marginBottom: 24,
                display: "flex",
                alignItems: "center",
                flexWrap: "wrap",
                gap: 8,
                padding: "10px 16px",
                background: "transparent"
            }}
        >
            <Link to="/">Home</Link>
            <span style={{ margin: "0 4px" }}>|</span>
            <Link to="/events">Events</Link>
            {user && (
                <>
                    <span style={{ margin: "0 4px" }}>|</span>
                    <Link to="/subscriptions">My Subscriptions</Link>
                </>
            )}
            <span style={{ margin: "0 8px" }}>|</span>
            {user ? (
                <>
                    <span>
                        Hello, <b>{user.username}</b> [{user.roles.join(", ")}]
                    </span>
                    <button
                        style={{
                            marginLeft: 8,
                            padding: "2px 12px",
                            fontSize: "0.95rem",
                            borderRadius: "4px",
                            background: "#1976d2",
                            color: "#fff",
                            border: "none",
                            cursor: "pointer",
                            verticalAlign: "middle",
                            display: "inline-block",
                            height: "auto",         // let it match the text height
                            lineHeight: "1.2",      // matches text line height
                            minWidth: "64px"
                        }}
                        onClick={handleLogout}
                    >
                        Logout
                    </button>

                </>
            ) : (
                <>
                    <Link to="/login">Login</Link>
                    <span style={{margin: "0 4px"}}>|</span>
                    <Link to="/signup">Signup</Link>
                </>
            )}
        </nav>
    );
};

export default Navbar;
