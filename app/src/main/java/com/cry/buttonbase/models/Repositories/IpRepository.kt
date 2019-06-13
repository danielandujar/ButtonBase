package com.cry.buttonbase.models.Repositories

import com.cry.buttonbase.models.Api.IpConfig.IpConfigClient
import com.cry.buttonbase.models.IpData

class IpRepository
{
    fun getIpData(onSuccess : (IpData) -> Unit)
    {
        IpConfigClient().loadIpData{
            onSuccess(it)
        }
    }
}