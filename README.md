# Hadis_search_engine_v2.0
### CURLS: https://documenter.getpostman.com/view/32100963/2sAYkHoJRF

### Expose localhost to public: 
Install WinGet on windows sandbox
```shell
$progressPreference = 'silentlyContinue'
Write-Host "Installing WinGet PowerShell module from PSGallery..."
Install-PackageProvider -Name NuGet -Force | Out-Null
Install-Module -Name Microsoft.WinGet.Client -Force -Repository PSGallery | Out-Null
Write-Host "Using Repair-WinGetPackageManager cmdlet to bootstrap WinGet..."
Repair-WinGetPackageManager
Write-Host "Done."
```
Install cloudflare using WinGet
```shell
winget install --id Cloudflare.cloudflared
```
Check cloudflare version (after installation)
```shell
cloudflared --version
```
Create a tunnel using cloudflare to expose localhost to public
``` shell
cloudflared tunnel --protocol http2 --url http://localhost:5050
```
