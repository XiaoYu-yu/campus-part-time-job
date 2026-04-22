param(
    [switch]$Full
)

$RepoRoot = Resolve-Path (Join-Path $PSScriptRoot "..\..")

Write-Host "Campus relay trial-operation command index"
Write-Host "Repo: $RepoRoot"
Write-Host ""

Write-Host "1. Lightweight preflight"
Write-Host "powershell -ExecutionPolicy Bypass -File scripts\trial-operation\preflight.ps1"
Write-Host ""

Write-Host "2. Preflight with sample validation"
Write-Host "powershell -ExecutionPolicy Bypass -File scripts\trial-operation\preflight.ps1 -RunSampleValidation"
Write-Host ""

Write-Host "3. Full compile/build preflight"
Write-Host "powershell -ExecutionPolicy Bypass -File scripts\trial-operation\preflight.ps1 -RunSampleValidation -RunBackendCompile -RunFrontendBuild"
Write-Host ""

Write-Host "4. Backend test profile with H2"
Write-Host "cd `"$RepoRoot\backend`""
Write-Host ".\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=test"
Write-Host ""

Write-Host "5. Frontend dev server"
Write-Host "cd `"$RepoRoot\frontend`""
Write-Host "npm run dev"
Write-Host ""

Write-Host "6. Manual H2 reset"
Write-Host "Stop the backend test-profile process, then start it again."
Write-Host "The test profile uses jdbc:h2:mem:takeaway_test with DB_CLOSE_DELAY=-1, so a backend process restart reloads schema-h2.sql and data-h2.sql."
Write-Host "This script intentionally does not kill processes or reset data automatically."
Write-Host ""

if ($Full) {
    Write-Host "7. Browser entrypoints"
    Write-Host "Frontend: http://localhost:5173"
    Write-Host "Backend:  http://localhost:8080"
    Write-Host "Customer onboarding: /user/campus/courier-onboarding"
    Write-Host "Customer result:     /user/campus/order-result"
    Write-Host "Courier workbench:   /courier/workbench"
    Write-Host "Admin settlement:    /campus/settlements"
    Write-Host "Admin after-sale:    /campus/after-sale-executions"
    Write-Host "Admin courier ops:   /campus/courier-ops"
    Write-Host "Admin exceptions:    /campus/exceptions"
    Write-Host ""
}

Write-Host "Boundary:"
Write-Host "- No bridge changes."
Write-Host "- No auth changes."
Write-Host "- No route or API changes."
Write-Host "- No automatic H2 reset."
Write-Host "- No long-running process is started by this command index."
