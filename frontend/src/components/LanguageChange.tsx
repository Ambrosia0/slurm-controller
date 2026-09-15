import "../styles/languageSelect.css"

import { useTranslation } from 'react-i18next';

const languages = [
    { code: 'en', name: "EN" },
    { code: 'ru', name: "RU" }
]

export const LanguageChange = () =>{
    const { i18n } = useTranslation();

    return(
        <select
            id="language-select"
            value={i18n.resolvedLanguage}
            onChange={(e) => i18n.changeLanguage(e.target.value)}
            className='language-select'
        >
            {languages.map(lang => 
                <option key={lang.code} value={lang.code}>
                    {lang.name}
                </option>
            )}
        </select>
    )
}