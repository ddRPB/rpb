/*
 * This file is part of RadPlanBio
 *
 * Copyright (C) 2013-2022 RPB Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.dktk.dd.rpb.core.util;

import de.dktk.dd.rpb.core.domain.pacs.DicomWebQuery;

import java.net.URI;

/**
 * Created by root on 4/21/17.
 */
public class DicomWebQueryUtil {

//    public static DicomWebQuery Parse(URI requestUri)
//    {
//        //var queryParams = HttpUtility.ParseQueryString(requestUri.Query);
//
//        DicomWebQuery query = new DicomWebQuery();
//
//        foreach (var queryParam in queryParams.AllKeys)
//        {
//            var value = queryParams[queryParam];
//            var queryParamLower = queryParam.ToLower();
//
//            if (queryParamLower == "includefield")
//            {
//                var values = value.Split(',');
//                foreach (var splitValue in values)
//                {
//                    var dicomTag = GetTag(splitValue);
//                    if (dicomTag == null)
//                    {
//                        query.getErrors().add(String.Format("include field specified unknown DICOM Keyword or Tag '{0}', skipping", queryParam));
//                        continue;
//
//                    }
//                    query.IncludeFields.Add(dicomTag);
//                }
//            }
//            else if (queryParamLower == "fuzzymatching")
//            {
//                query.getFuzzyMatching() = value.ToLower() == "false";
//            }
//            else if (queryParamLower == "limit")
//            {
//                query.getLimit() = int.Parse(value);
//            }
//            else if (queryParamLower == "offset")
//            {
//                query.getOffset() = int.Parse(value);
//            }
//            else
//            {
//                // must be an attribute,
//                var dicomTag = GetTag(queryParam);
//                if (dicomTag == null)
//                {
//                    query.Errors.Add(String.Format("unknown DICOM Keyword or Tag '{0}' specified, skipping", queryParam));
//                    continue;
//                }
//                var queryAttribute = new QueryAttribute();
//                queryAttribute.RawKey = queryParam;
//                queryAttribute.RawValue = value;
//                queryAttribute.Tag = dicomTag;
//                query.QueryAttributes.Add(dicomTag, queryAttribute);
//            }
//        }
//        return query;
//    }

//    private static DicomTag GetTag(string dicomTagOrKeyword)
//    {
//        var entry = DICOMTagOrKeywordLookup.Instance().Lookup(dicomTagOrKeyword);
//        if (entry == null)
//        {
//            return null;
//        }
//        return entry.Tag;
//    }

}
