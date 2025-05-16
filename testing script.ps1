# ---------- Config ----------
$GatewayUrl = "http://localhost:8081"
$authUrl    = "${GatewayUrl}/api/auth"
$eventsUrl  = "${GatewayUrl}/api/events"

function New-RandomString([int]$length = 8) {
    -join ((65..90) + (97..122) | Get-Random -Count $length | % {[char]$_})
}

# ---------- VIP User Setup ----------
$vipUser = "vip_" + (New-RandomString 6)
$vipPass = "TestPwd123!"
Write-Host "`n[VIP] Signing up VIP user `${vipUser}`..."

$signupBody = @{
    username = $vipUser
    password = $vipPass
    roles    = "ROLE_VIP"
} | ConvertTo-Json
try {
    Invoke-RestMethod -Uri "${authUrl}/signup" -Method Post -Body $signupBody -ContentType 'application/json'
    Write-Host "Signup done."
} catch {
    Write-Host "Signup failed: $($_.Exception.Message)"
    exit
}

Write-Host "`n[VIP] Logging in as `${vipUser}`..."
$loginBody = @{
    username = $vipUser
    password = $vipPass
} | ConvertTo-Json
try {
    $loginResponse = Invoke-RestMethod -Uri "${authUrl}/login" -Method Post -Body $loginBody -ContentType 'application/json'
    $vipToken = $loginResponse.token
    if (-not $vipToken) { throw "No token returned!" }
    $vipHeaders = @{ "Authorization" = "Bearer ${vipToken}" }
    Write-Host "Login done."
} catch {
    Write-Host "Login failed: $($_.Exception.Message)"
    exit
}

# ---------- Create 10 Events ----------
$eventIds = @()
for ($i = 1; $i -le 10; $i++) {
    $eventBody = @{
        title       = "VIP Event $i"
        description = "Event $i created by ${vipUser}"
        pdfPath     = "vip-event-$i.pdf"
    } | ConvertTo-Json
    try {
        $event = Invoke-RestMethod -Uri $eventsUrl -Method Post -Body $eventBody -ContentType 'application/json' -Headers $vipHeaders
        $eventId = $event.id
        $eventIds += $eventId
        Write-Host "Created event $i with id $eventId"
    } catch {
        Write-Host "Event creation $i failed: $($_.Exception.Message)"
    }
}

# ---------- Regular User Setup ----------
$user = "user_" + (New-RandomString 6)
$pass = "TestPwd123!"
Write-Host "`n[REGULAR] Signing up regular user `${user}`..."

$signupBody = @{
    username = $user
    password = $pass
    roles    = "ROLE_USER"
} | ConvertTo-Json
try {
    Invoke-RestMethod -Uri "${authUrl}/signup" -Method Post -Body $signupBody -ContentType 'application/json'
    Write-Host "Signup done."
} catch {
    Write-Host "Signup failed: $($_.Exception.Message)"
    exit
}
Write-Host "`n[REGULAR] Logging in as `${user}`..."

$loginBody = @{
    username = $user
    password = $pass
} | ConvertTo-Json
try {
    $loginResponse = Invoke-RestMethod -Uri "${authUrl}/login" -Method Post -Body $loginBody -ContentType 'application/json'
    $token = $loginResponse.token
    if (-not $token) { throw "No token returned!" }
    $headers = @{ "Authorization" = "Bearer ${token}" }
    Write-Host "Login done."
} catch {
    Write-Host "Login failed: $($_.Exception.Message)"
    exit
}

# ---------- Subscribe Regular User to All Events ----------
Write-Host "`n[REGULAR] Subscribing to all events..."
foreach ($eventId in $eventIds) {
    try {
        Invoke-RestMethod -Uri "${eventsUrl}/${eventId}/subscribe" -Method Post -Headers $headers
        Write-Host "Subscribed to event ${eventId}"
    } catch {
        Write-Host "Subscription to event ${eventId} failed: $($_.Exception.Message)"
    }
}

# ---------- List All Events for Regular User ----------
Write-Host "`n[REGULAR] Listing all events (should see all):"
try {
    $events = Invoke-RestMethod -Uri $eventsUrl -Headers $headers
    $events | Format-Table
} catch {
    Write-Host "Event listing failed: $($_.Exception.Message)"
}

# ---------- Unsubscribe from First 5 Events ----------
Write-Host "`n[REGULAR] Unsubscribing from first 5 events..."
foreach ($eventId in $eventIds[0..4]) {
    try {
        Invoke-RestMethod -Uri "${eventsUrl}/${eventId}/unsubscribe" -Method Delete -Headers $headers
        Write-Host "Unsubscribed from event ${eventId}"
    } catch {
        Write-Host "Unsubscription from event ${eventId} failed: $($_.Exception.Message)"
    }
}

# ---------- List User's Current Subscriptions ----------
Write-Host "`n[REGULAR] Listing current subscriptions after unsubscribe:"
try {
    $subs = Invoke-RestMethod -Uri "${eventsUrl}/subscriptions?username=${user}" -Headers $headers
    if ($subs) {
        Write-Host "User is currently subscribed to the following event IDs:"
        $subs | ForEach-Object { Write-Host " - $_" }
    } else {
        Write-Host "User is not subscribed to any events."
    }
} catch {
    Write-Host "Could not fetch subscriptions: $($_.Exception.Message)"
}

# ---------- Final Event List for Regular User ----------
Write-Host "`n[REGULAR] Final event list (should still see all):"
try {
    $events = Invoke-RestMethod -Uri $eventsUrl -Headers $headers
    $events | Format-Table
} catch {
    Write-Host "Final event listing failed: $($_.Exception.Message)"
}

Write-Host "`n[REGULAR] Finished test for user `${user}`!"
