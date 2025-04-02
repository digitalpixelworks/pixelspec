# Security Policy  

## Supported Versions  
Only the **latest stable release** receives security updates.

## Reporting Vulnerabilities 🚨  
**Please disclose responsibly** by emailing:  
[digitalpixelworks01@gmail.com](mailto:digitalpixelworks01@gmail.com)  

- Include **steps to reproduce** and **device info**  
- Expect a response within **72 hours**  
- Public disclosures require **written approval**  

## Security Measures 🔐  
1. **No unnecessary permissions**  
   - Only `BATTERY_STATS` and `INTERNET` (for crash reporting)  
2. **All data processed locally** - No cloud sync/transmission  
3. **Regular audits** with:  
   - [MobSF](https://github.com/MobSF/Mobile-Security-Framework-MobSF)  
   - Android Studio's **Code Analysis**  

## Known Risks ⚠️  
- Battery temperature readings may be **inaccurate** on some devices  
- Root detection can be **bypassed** (non-critical)  