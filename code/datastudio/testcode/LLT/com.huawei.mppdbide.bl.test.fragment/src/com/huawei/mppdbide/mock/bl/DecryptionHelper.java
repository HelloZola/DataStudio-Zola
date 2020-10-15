package com.huawei.mppdbide.mock.bl;

import com.huawei.mppdbide.utils.security.DecryptionUtil;
import com.huawei.mppdbide.utils.security.SecureUtil;

public class DecryptionHelper extends DecryptionUtil
{

    private boolean throwNoSuchAlogoException;
    private boolean throwInvalidKeyException;
    private boolean throwNoSuchPaddingException;
    private boolean throwInvalidAlgorithmParameterException;
    private boolean throwIllegalBlockSizeException;
    private boolean throwBadPaddingException;
    
    public DecryptionHelper(SecureUtil encryptionDecryption)
    {
        super(encryptionDecryption);
    }

    public void setThrowNoSuchAlogoException(boolean throwNoSuchAlogoException)
    {
        this.throwNoSuchAlogoException = throwNoSuchAlogoException;
    }
    
    public void setThrowInvalidKeyException(boolean throwInvalidKeyException)
    {
        this.throwInvalidKeyException = throwInvalidKeyException;
    }
    
    public void setThrowNoSuchPaddingException(
            boolean throwNoSuchPaddingException)
    {
        this.throwNoSuchPaddingException = throwNoSuchPaddingException;
    }
    
    public void setThrowInvalidAlgorithmParameterException(
            boolean throwInvalidAlgorithmParameterException)
    {
        this.throwInvalidAlgorithmParameterException = throwInvalidAlgorithmParameterException;
    }
    
    public void setThrowIllegalBlockSizeException(
            boolean throwIllegalBlockSizeException)
    {
        this.throwIllegalBlockSizeException = throwIllegalBlockSizeException;
    }
    
    public void setThrowBadPaddingException(boolean throwBadPaddingException)
    {
        this.throwBadPaddingException = throwBadPaddingException;
    }
    
}
